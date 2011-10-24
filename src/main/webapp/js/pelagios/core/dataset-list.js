Pelagios.DatasetList = {}

Pelagios.DatasetList.PANEL_DIV_ID = "dataset-list";

Pelagios.DatasetList.getInstance = function() {
	if (Pelagios.DatasetList.instance)
		return Pelagios.DatasetList.instance;
	
	Pelagios.DatasetList.instance = {
		init : function() {
		    Pelagios.Async.getInstance().datasets(null, function(data) {
				for (var i=0, ii=data.length; i<ii; i++) {
					Pelagios.DatasetList.instance.add(data[i]);
				}
		    });	
		},
		
		add : function(dataset) {
			console.log(dataset);
			document.getElementById(Pelagios.DatasetList.PANEL_DIV_ID)
				.innerHTML += '<span>' + dataset.name + '</span>'; 
		}
	}
	
	return Pelagios.DatasetList.instance;
}