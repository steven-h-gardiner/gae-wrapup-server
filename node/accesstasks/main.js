var at = {};

at.mods = {};
at.mods.cp = require('child_process');
at.mods.stdlog = require('stdlog');
at.mods.luigi = require('luigi');

at.logger = at.mods.stdlog.call();

at.rez = {};

at.rez.wrapup = {};
at.rez.wrapup.prefix = "https://gae-wrapup-server.appspot.com";
at.rez.wrapup.path = "/access/tasklist.csv";
at.rez.swauth = {};
at.rez.swauth.prefix = "https://sw-auth.appspot.com";
at.rez.swauth.path = "/admin/blob";
at.rez.swauth.token = "abcd1253";

at.procs = {};

at.eq = process;

at.getBlobUrl = function(spec) {
  return [[at.rez.swauth.prefix,at.rez.swauth.path].join(""),
	  [["token", at.rez.swauth.token].join("="),
	   ["type", spec.type].join("="),
	   ["reqid", spec.respid].join("=")].join("&")].join("?");
};

at.eq.on('task', function(spec) {
  spec = spec || {};

  spec.fields = spec.line.split(/,/);
  spec.taskid = spec.fields.shift();
  spec.respid = spec.fields.shift();
  
  spec.downloads = spec.downloads || {};

  spec.downloads.examples = {
    localdir: spec.respid,
    localfile: [spec.respid, 'examples.json'].join("/"),
    url: at.getBlobUrl({respid:spec.respid,type:"examples"}),
  };
  spec.downloads.dom = {
    localdir: spec.respid,
    localfile: [spec.respid, 'document.xhtml'].join("/"),
    url: at.getBlobUrl({respid:spec.respid,type:"document"}),
  };
  spec.downloads.meta = {
    localdir: spec.respid,
    localfile: [spec.respid, 'meta.json'].join("/"),
    url: at.getBlobUrl({respid:spec.respid,type:"metainfo"}),
  };

});
at.eq.on('tasklist', function() {
  at.logger.info('hi');

  at.procs.curl = at.mods.cp.spawn('curl', ['-s', [at.rez.wrapup.prefix, at.rez.wrapup.path].join("")]);

  if (true) {
    at.procs.curltee = at.mods.cp.spawn('tee', ['/tmp/wrapup_curl.txt']);
    at.procs.scrtee = at.mods.cp.spawn('tee', ['/tmp/fetch_sw.sh']);
  }

  at.procs.filter = new at.mods.luigi.filter();
  at.procs.filter.on('line', function(line) {
    var that = this;
 
    console.error("LINE: %s", line);

    var spec = {};
    var fields = line.split(/,/);
    spec.taskid = fields.shift();
    spec.respid = fields.shift();

    spec.line = line;
    spec.downloads = {};
    at.eq.emit('task', spec);

    console.error('task %j', spec);
    at.logger.info('task %j', spec);

    Object.keys(spec.downloads).forEach(function(key) {
      var download = spec.downloads[key];
      that.output.write([//"#",
			 "mkdir"
			 , "-p", download.localdir
			 , "\n" ].join(" "));
      that.output.write(["ls", download.localfile, '||', "\n"].join(" "));
      that.output.write(["(", "\n"].join(" "));     
      at.rez.archives.forEach(function(arch) {
        that.output.write(["tar",
			   "xvzOf", arch,
			   ["req",download.localfile].join("/"),
			   "||",
			   "\n" ].join(" "));
      });
      that.output.write([//"#",
			 "curl"
			 //, "-s" 
			 //, "-o", download.localfile
			 , ["'",download.url,"'"].join("")
			 , "\n" ].join(" "));
      that.output.write([")", ">", download.localfile, "\n"].join(" "));
    });
    that.output.write(["echo"
		       , ["'", line, "'"].join("")
		       , "\n" ].join(" "));
  });


  at.procs.swfetch = at.mods.cp.spawn('bash', ['-']);

  at.procs.filter2 = new at.mods.luigi.filter();
  at.procs.filter2.on('line', function(line) {
    var that = this;
 
    console.error("LINE2: %s", line);

    var spec = {};

    spec.line = line;
    spec.downloads = {};
    at.eq.emit('task', spec);

    ["0"
     //,"1"
     //,"2"
     //,"3"
     ].forEach(function(condition) {
	 that.output.write(["java"
			    , "-jar", "./smartwrap-cli.jar"
			    , "-e", spec.downloads.examples.localfile
			    , "-d", spec.downloads.dom.localfile
			    , "-i", spec.downloads.meta.localfile
			    , "--format", "xhtml"
			    , "-o", [spec.taskid, condition, '.xhtml'].join("")
			    , "\n"].join(" "));
    });
  });

  if (at.procs.curltee) {
    at.procs.curl.stdout.pipe(at.procs.curltee.stdin);
    at.procs.curltee.stdout.pipe(at.procs.filter.stdin);
  } else {
    at.procs.curl.stdout.pipe(at.procs.filter.stdin);
  }
  at.procs.curl.stderr.pipe(process.stderr);

  if (at.procs.scrtee) {
    at.procs.filter.stdout.pipe(at.procs.scrtee.stdin);
    at.procs.scrtee.stdout.pipe(at.procs.swfetch.stdin);
  } else {
    at.procs.filter.stdout.pipe(at.procs.swfetch.stdin);
  }

  at.procs.swfetch.stdout.pipe(at.procs.filter2.stdin);
  at.procs.swfetch.stderr.pipe(process.stderr);

  at.procs.filter2.stdout.pipe(process.stdout);

});

at.eq.on('archives', function(spec) {
  at.rez.archives = [];

  at.procs.find = at.mods.cp.spawn('find', 
				   [spec.root,
				    '-name', '*.tgz']);

  at.procs.ffilt = new at.mods.luigi.filter();
  at.procs.ffilt.on('line', function(line) {
    at.rez.archives.push(line);  
  });

  at.procs.find.stdout.pipe(at.procs.ffilt.stdin);
  at.procs.find.stderr.pipe(process.stderr);

  at.procs.ffilt.stdout.on('end', function() {
    at.procs = {};
    console.error("ARCHIVES %j", at.rez.archives);

    at.eq.emit('tasklist');
  });
});

at.eq.emit('archives', {root:'./archive'});
