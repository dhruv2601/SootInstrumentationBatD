var parseAPK = require('apk-parser');

// to add cmd arg in js, just add the arg after the whole command without changing anything else
const args = process.argv;
console.log(args[2]);
var packageName = args[2];

parseAPK(packageName, 8*1024*1024*1024, function(err, data)
{

	if(err!=undefined)
	{
		var obj = '{ }';
		var fs = require("fs");
		fs.writeFile("./ExtractorJSONoutput.json", JSON.stringify(obj), (err) => {
			console.log("File is empty")
		})
	}
	else
	{
		var fs = require("fs");
		fs.writeFile("./ExtractorJSONoutput.json", JSON.stringify(data), (err) => {
			console.log("File created")
		})
	}
	// console.log(data)
	// console.log(err)
});