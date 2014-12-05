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

at.rez.extratags = [
  "header",
  "section",
  "nav",
  "data",
  "footer",
  "time",
  "svg",
  "g",
  "path",
  "canvas",
  "fblike",
  "sfmsg",
];

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
			 , "-p", ["experiments","data",download.localdir].join("/")
			 , "\n" ].join(" "));
      that.output.write(["test", "-f", ["experiments","data",download.localfile].join("/"), '||', "\n"].join(" "));
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
      that.output.write([")", ">", ["experiments","data",download.localfile].join("/"), "\n"].join(" "));
    });
    that.output.write(["echo"
		       , ["'", line, "'"].join("")
		       , "\n" ].join(" "));
  });


  at.procs.swfetch = at.mods.cp.spawn('bash', ['-c',
                                               ["tee /tmp/swfetch.sh",
                                                "bash -"].join(" | ")]);

  at.procs.filter2 = new at.mods.luigi.filter();
  at.procs.filter2.on('line', function(line) {
    var that = this;
 
    console.error("LINE2: %s", line);

    var spec = {};

    spec.line = line;
    spec.downloads = {};
    at.eq.emit('task', spec);

    that.output.write(["java"
		       , "-jar", require.resolve("./smartwrap-cli.jar")
		       , "-e", ["experiments","data",spec.downloads.examples.localfile].join("/")
		       , "-d", ["experiments","data",spec.downloads.dom.localfile].join("/")
		       , "-i", ["experiments","data",spec.downloads.meta.localfile].join("/")
		       , "--format", "xhtml"
		       , "-o", ["war", "atasks", [spec.taskid, '.xhtml'].join("")].join("/")
		       , "\n"].join(" "));
    that.output.write(["ls"
		       , ["war", "atasks", [spec.taskid, '.xhtml'].join("")].join("/")
		       , "\n"].join(" "));
  });

  at.procs.exec2 = at.mods.cp.spawn('bash', ['-c',
                                             ["tee /tmp/swwrap.sh",
                                              "bash -"].join(" | ")]);

  var perl = "s/\\&\\#195;\\&\\#8218;\\&\\#194;//g;";
  console.error("PERL %s", perl);
  
  at.procs.fix = at.mods.cp.spawn('parallel',
				  [["tidy", "-utf8", "-quiet", "--show-warnings", "0", 
				    "--new-blocklevel-tags", at.rez.extratags.join(","),
				    "-asxml", "-numeric", "{}", 
                                    '2>>/tmp/tidy.err',
				    "|", "tee", "{.}00.xhtml",
				    "|", "perl", "-pe", ['"',perl,'"'].join(""),
				    "|", "xsltproc", require.resolve("./general.xsl"), "-",
				    "|", "tee", "{.}0.xhtml",
				    "|", "xsltproc", require.resolve("./underuse.xsl"), "-",
				    "|", "tee", "{.}1.xhtml",
				    "|", "xsltproc", require.resolve("./overuse.xsl"), "-",
				    ">", "{.}3.xhtml"].join(" ")]);
  
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

  at.procs.filter2.stdout.pipe(at.procs.exec2.stdin);

  at.procs.exec2.stdout.pipe(at.procs.fix.stdin);
  at.procs.exec2.stderr.pipe(process.stderr);
  
  at.procs.fix.stdout.pipe(process.stdout);
  at.procs.fix.stderr.pipe(process.stderr);


  
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
