// Initialize variables with default values
var longitude = 3.3775515;
var latitude = 6.5115311;
var srid = 4326;
var geoJson = {"crs": {"type": "link", "properties": {"href": "http://spatialreference.org/ref/epsg/4326/", "type": "proj4"}},
                "type": "FeatureCollection",
                "features": [{"geometry": {"type": "Point", "coordinates": [3.3781779999999886, 6.5056559999999966]}, "type": "Feature",
                                "properties": {"distance": 662.0, "name": ["POLICE"], "picture":
                                "/media/piclist/2016/07/26/police22.png", "did": 38, "picture2":
                                "/media/piclist/2016/07/26/police_station.png", "address": "Oyadiran St, Lagos, Nigeria",
                                "id": "POLICE"}},
			                {"geometry": {"type": "Point", "coordinates": [3.364241999999998, 6.504802999999988]}, "type": "Feature", 
								"properties": {"distance": 1663.0, "name": ["POLICE"], "picture": "/media/piclist/2016/07/26/police22.png",
                                "did": 39, "picture2": "/media/piclist/2016/07/26/police_station.png", "address": "Aiyeleto St, Lagos, Nigeria",
                                "id": "POLICE"}},
			                {"geometry": {"type": "Point", "coordinates": [3.353865999999988, 6.517038000000005]}, "type": "Feature", 
								"properties": {"distance": 2708.0, "name":
                                ["POLICE"], "picture": "/media/piclist/2016/07/26/police22.png", "did": 33, "picture2":
                                "/media/piclist/2016/07/26/police_station.png", "address": "Main Gate Rd, LUTH, Lagos, Nigeria", "id": "POLICE"}}
             ]};

function Point(longitude,latitude,srid){
    this.longitude = longitude;
    this.latitude = latitude;
    this.srid  = srid;
}

$(document).ready(function() {
	// Initialize related input elements with default values
	$("#longitude").val(longitude);
	$("#latitude").val(latitude);
	$("#srid").val(srid);
	$("#geojson").val(JSON.stringify(geoJson));
	
	
	$("#findClosest").click(function() {
		findClosestFeature();
	});
})

function findClosestFeature() {
    // Retrieve parameter values (IFF available). Defaults would be used otherwise
    if ($("#longitude").val() != "") {
        longitude = $("#longitude").val();
    }	
    if ($("#latitude").val() != "") {
        latitude = $("#latitude").val();
    }
    if ($("#srid").val() != "") {
        srid = $("#srid").val();
    }
    if ($("#geojson").val() != "") {
        geoJson = JSON.parse($("#geojson").val());
    }		
	
    // Create a Point object from given parameters (Longitude, Latitude, and Srid)
    var pointA = new Point(longitude,latitude,srid);	
    // Find the nearest feature in the GeoJSON to the given point
    var nearestFeature = findNearestFeature(geoJson,pointA);	
    // Display the closest feature and distance to the point	
    $("#distance").val(nearestFeature["distance"]);
    $("#closestFeature").val(JSON.stringify(nearestFeature["feature"]));
	
	/* OUTPUT for default input values: The distance and feature is returned as JSON
		 {"feature":{"geometry":{"type":"Point","coordinates":[3.3781779999999886,6.5056559999999966]},
					"type":"Feature","properties":{"distance":662,"name":["POLICE"],"picture":"/media/piclist/2016/07/26/police22.png",
					"did":38,"picture2":"/media/piclist/2016/07/26/police_station.png","address":"Oyadiran St, Lagos, Nigeria","id":"POLICE"}},
		  "distance":0.005908409452638361}
	*/
}

// Find the closest feature to a point
function findNearestFeature(geoJson,pointA) {
    var closestFeatureObj = null;

    // Retrieve coordinates from JSON Object
    var features = geoJson["features"];	
    var closestFeature = features[0];	
    var coordinates = closestFeature["geometry"]["coordinates"];	

    // Find closest distance by computing the distance of each feature to the point
    var closestDistance = getDistance(new Point(coordinates[0],coordinates[1]),pointA);	
    for (var i=1; i<features.length; i++) {		
        var feature = features[i];		
        coordinates = feature["geometry"]["coordinates"];		
        var distance = getDistance(new Point(coordinates[0],coordinates[1]),pointA);		
        if (distance < closestDistance) {
            closestFeature = feature;
            closestDistance = distance;
        }
    }

    // Construct JSON Output from closest feature and distance
    closestFeatureObj = {};    
    closestFeatureObj["feature"] = closestFeature;
    closestFeatureObj["distance"] = closestDistance;
    return closestFeatureObj;
}

// Calculates the distance between 2 geometry points using pythagoras' theorem
function getDistance(Point1, Point2) {
    var x = Point2.longitude - Point1.longitude;
    var y = Point2.latitude - Point1.latitude;
    return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
}