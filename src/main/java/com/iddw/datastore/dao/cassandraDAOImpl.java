package com.iddw.datastore.dao;

import java.util.Properties;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

import com.iddw.datastore.util.const;

public class CassandraDAOImpl implements DAO<AttributeBlob> {
	
	@Autowired
	@Qualifier("envProperties")
	private Properties envProperties;
	
    // Cassandra keyspace
    private static Keyspace ks;

    // Data model is documented in the wiki
    private static final ColumnFamily<String, String> CF_SUBSCRIPTIONS = new ColumnFamily<String, String>("Subscriptions", StringSerializer.get(), StringSerializer.get());

    /*
    *//**
     * Get the feed urls from Cassandra
     *//*
    @Override
    public List<String> getSubscribedUrls(String userId) throws Exception{
        OperationResult<ColumnList<String>> response;
        try {
            response = getKeyspace().prepareQuery(CF_SUBSCRIPTIONS).getKey(userId).execute();
        } catch (NotFoundException e) {
            logger.error("No record found for this user: " + userId);
            throw e;
        } catch (Exception t) {
            logger.error("Exception occurred when fetching from Cassandra: " + t);
            throw t;
        }

        final List<String> items = new ArrayList<String>();
        if (response != null) {
            final ColumnList<String> columns = response.getResult();
            for (Column<String> column : columns) {
                items.add(column.getName());
            }
        }

        return items;
    }

    *//**
     * Add feed url into Cassandra
     *//*
    @Override
    public void subscribeUrl(String userId, String url) throws Exception{
        try {
            OperationResult<Void> opr = getKeyspace().prepareColumnMutation(CF_SUBSCRIPTIONS, userId, url)
                .putValue("1", null).execute();
            logger.info("Time taken to add to Cassandra (in ms): " + opr.getLatency(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            logger.error("Exception occurred when writing to Cassandra: " + e);
            throw e;
        }
    }

    *//**
     * Delete feed url from Cassandra
     *//*
    @Override
    public void unsubscribeUrl(String userId, String url) throws Exception{
        try {
            OperationResult<Void> opr = getKeyspace().prepareColumnMutation(CF_SUBSCRIPTIONS, userId, url)
                .deleteColumn().execute();
            logger.info("Time taken to delete from Cassandra (in ms): " + opr.getLatency(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            logger.error("Exception occurred when writing to Cassandra: " + e);
            throw e;
        }
    }*/


    /*
      Connect to Cassandra
     */
    private static Keyspace getKeyspace() throws Exception{
        if (ks == null) {
            try {
                AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
                    .forKeyspace(DynamicPropertyFactory.getInstance().getStringProperty(RSSConstants.CASSANDRA_KEYSPACE, null).get())
                    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
                        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
                    )
                    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
                        .setPort(DynamicPropertyFactory.getInstance().getIntProperty(RSSConstants.CASSANDRA_PORT, 0).get())
                        .setMaxConnsPerHost(DynamicPropertyFactory.getInstance().getIntProperty(RSSConstants.CASSANDRA_MAXCONNSPERHOST, 1).get())
                        .setSeeds(DynamicPropertyFactory.getInstance().getStringProperty(RSSConstants.CASSANDRA_HOST, "").get() + ":" +
                                  DynamicPropertyFactory.getInstance().getIntProperty(RSSConstants.CASSANDRA_PORT, 0).get()
                        )
                    )
                    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                    .buildKeyspace(ThriftFamilyFactory.getInstance());

                context.start();
                ks = context.getEntity();
            } catch (Exception e) {
                logger.error("Exception occurred when initializing Cassandra keyspace: " + e);
                throw e;
            }
        }

        return ks;
    }
}