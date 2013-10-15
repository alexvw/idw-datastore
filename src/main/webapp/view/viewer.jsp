<html>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<body>
    <h2>C* Datastore Viewer</h2>
    <p>
        ${output}
    </p>  
</body>
<script>
function goAjax(){
	//var object = [{rowId:'rowid1234',attrId:'attrid23434',blob:'blobtext'}];
	var object = [{rowId:'rowid2343', attrId:'attr1', blob:'bloctext'},{rowId:'row5454', attrId:'attr2',blob:'blobltext'}];
	 $.ajax({
	    	headers: { 
	            'Content-Type': 'application/json' 
	        },
	        type: "POST",
	        url: 'http://localhost:8080/write',
	        data: JSON.stringify( object ),
	        dataType: "json",
	        success: function(data) {
	           alert(data);
	        }
	    });
}
</script>
</html>