/**
 * Application extra parameters.
 * 
 * <ul>
 * <li><b>debugActiveWhenScript: </b>Is debug active when running script mode?</li>
 * <li><b>chunkSize: </b>Project grid chunks size.</li>
 * <li><b>reportAutoSaveDelay: </b>Delay (in ms) between each report auto-save.</li>
 * <ul>
 */
var ExtraParameters = {
	debugActiveWhenScript : false,
	chunkSize : 2,
	reportAutoSaveDelay : 120000
};

window.addEventListener('load', function() {
	var scripts = ['//www.openstreetmap.org/openlayers/OpenStreetMap.js'];
	for(var index = 0; index < scripts.length; index++) {
		var script = document.createElement('script');
		script.src = scripts[index];
		script.async = true;
		document.body.appendChild(script);
	}
});