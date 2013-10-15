package com.iddw.datastore.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.connectionpool.impl.SimpleAuthenticationCredentials;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

import com.iddw.datastore.dao.GenericDAO;
import com.iddw.datastore.dataobject.AttrUIObject;
import com.iddw.datastore.dataobject.AttributeBlob;
import com.iddw.datastore.dataobject.RowUIObject;

public class CassandraDAOImpl implements GenericDAO<AttributeBlob> {
	
	@Autowired
	@Qualifier("envProperties")
	private static Properties envProperties;
	
    // Cassandra keyspace
    private static Keyspace ks;

    // Data model is documented in the wiki
    private static final ColumnFamily<String, String> CF_ESTORE = new ColumnFamily<String, String>("encryptedstore", StringSerializer.get(), StringSerializer.get());
    
    /**
     * Get an entire row
     */
   @Override
    public List<AttributeBlob> read(String rowId) throws Exception{
        OperationResult<ColumnList<String>> response;
        try {
            response = getKeyspace().prepareQuery(CF_ESTORE).getKey(rowId).execute();
        } catch (NotFoundException e) {
            //logger.error("No record found for this rowId: " + rowId);
            throw e;
        } catch (Exception t) {
            //logger.error("Exception occurred when fetching from Cassandra: " + t);
            throw t;
        }

        List<AttributeBlob> items = new ArrayList<AttributeBlob>();
        if (response != null) {
            final ColumnList<String> columns = response.getResult();
            for (Column<String> column : columns) {
            	AttributeBlob newBlob = new AttributeBlob();
            	//rowId, column.getName(), column.getStringValue()
            	newBlob.setAttrId(column.getName());
            	newBlob.setBlob(column.getStringValue());
            	newBlob.setRowId(rowId);
                items.add(newBlob);
            }
        }

        return items;
    }
   
   /**
    * Get a single attr column
    */
  @Override
   public AttributeBlob read(String rowId, String attrId) throws Exception{
       OperationResult<ColumnList<String>> response;
       try {
           response = getKeyspace().prepareQuery(CF_ESTORE).getKey(rowId).execute();
       } catch (NotFoundException e) {
           //logger.error("No record found for this rowId: " + rowId);
           throw e;
       } catch (Exception t) {
           //logger.error("Exception occurred when fetching from Cassandra: " + t);
           throw t;
       }
       
       //grab columns, then grab this attr blob
       final ColumnList<String> columns = response.getResult();
       String blob = columns.getColumnByName(attrId).getStringValue();

       AttributeBlob newBlob = new AttributeBlob();
   	//rowId, column.getName(), column.getStringValue()
	   	newBlob.setAttrId(attrId);
	   	newBlob.setBlob(blob);
	   	newBlob.setRowId(rowId);
       
       //return new AttributeBlob(rowId, attrId, blob);
       return newBlob;
   }

    /*
     * Insert encrypted blob into datastore
     */
    @Override
    public void write(List<AttributeBlob> blobs) throws Exception{
        try {
        	//check for null list
        	if (blobs != null) {
        		//moment of truth. Inserting DATA
        		MutationBatch m = getKeyspace().prepareMutationBatch();

        		for (AttributeBlob AB : blobs){
        			/*if (AB.getExpiry() != -1)
        			m.withRow(CF_ESTORE, AB.getRowId())
        			  .putColumn(AB.getAttrId(), AB.getBlob(), AB.getExpiry());*/
        			//else
        				m.withRow(CF_ESTORE, AB.getRowId())
      			  .putColumn(AB.getAttrId(), AB.getBlob(), null);
        		}
        		//GO FOR IT
        		 OperationResult<Void> opr = m.execute();
        		 
                //logger.info("Time taken to add to Cassandra (in ms): " + opr.getLatency(TimeUnit.MILLISECONDS));
        		 System.out.println("Successful write: " +opr.getLatency(TimeUnit.MILLISECONDS));
        	}
        } catch (Exception e) {
            //logger.error("Exception occurred when writing to Cassandra: " + e);
            throw e;
        }
    }

    /*
     * Delete single attribute blob
     */
   @Override
    public void deleteAttr(List<AttrUIObject> attrList) throws Exception{
        try {
        	MutationBatch m = getKeyspace().prepareMutationBatch();
        	for (AttrUIObject auo : attrList){
        		m.withRow(CF_ESTORE, auo.getRowId()).deleteColumn(auo.getAttrId());
        	}
           // OperationResult<Void> opr = getKeyspace().prepareColumnMutation(CF_ESTORE, rowId, attrId).deleteColumn().execute();
        	m.execute();
            //logger.info("Time taken to delete from Cassandra (in ms): " + opr.getLatency(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            //logger.error("Exception occurred when writing to Cassandra: " + e);
            throw e;
        }
    }
   
   /*
    * Delete entire user row
    */
  @Override
   public void deleteRow(List<RowUIObject> rowList) throws Exception{
       try {
    	   MutationBatch m = getKeyspace().prepareMutationBatch();
    	   for (RowUIObject ruo : rowList){
    		   m.withRow(CF_ESTORE, ruo.getRowId()).delete();
    	   }
           OperationResult<Void> opr = m.execute();
           //logger.info("Time taken to delete from Cassandra (in ms): " + opr.getLatency(TimeUnit.MILLISECONDS));
       } catch (Exception e) {
           //logger.error("Exception occurred when writing to Cassandra: " + e);
           throw e;
       }
   }


    /*
      Connect to Cassandra
     */
    private static Keyspace getKeyspace() throws Exception{
        if (ks == null) {
            try {
            	System.out.println(envProperties);
                /*AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
                    .forKeyspace(envProperties.getProperty("c.keyspace"))
                    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
                    	.setTargetCassandraVersion(envProperties.getProperty("c.cass.version"))
                        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
                    )
                    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("IDWConnectionPool")
                        .setPort(Integer.parseInt(envProperties.getProperty("c.port")))
                        .setMaxConnsPerHost(Integer.parseInt(envProperties.getProperty("c.maxconn")))
                        .setSeeds(envProperties.getProperty("c.host") + ":" + envProperties.getProperty("c.port")
                        )
                    )
                    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                    .buildKeyspace(ThriftFamilyFactory.getInstance());*/
            	
            	AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
                .forKeyspace("exampledatastore")
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
                	.setTargetCassandraVersion("1.2")
                    .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
                )
                .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
                	.setPort(9160)
                    .setMaxConnsPerHost(1)
                    .setSeeds("127.0.0.1:9160")
                    .setAuthenticationCredentials(
                    		 new SimpleAuthenticationCredentials(new String("cassandra"), new String("Criterion123"))
                    		 )
                )
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());

                context.start();
                ks = context.getClient();
            } catch (Exception e) {
                //logger.error("Exception occurred when initializing Cassandra keyspace: " + e);
                throw e;
            }
        }

        return ks;
    }
}