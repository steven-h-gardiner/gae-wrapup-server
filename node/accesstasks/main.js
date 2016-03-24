var at = {};

at.mods = {};
at.mods.cp = require('child_process');
at.mods.stdlog = require('stdlog');
//at.mods.luigi = require('luigi');
at.mods.eachline = require('eachline');

at.logger = at.mods.stdlog.call();

at.rez = {};

at.rez.wrapup = {};
at.rez.wrapup.prefix = "https://1412-dot-gae-wrapup-server.appspot.com";
at.rez.wrapup.path = "/access/tasklist.csv";
at.rez.swauth = {};
at.rez.swauth.prefix = "https://sw-auth.appspot.com";
at.rez.swauth.path = "/admin/blob";
at.rez.swauth.token = "abcd1253";

at.rez.extratags = [
  "article",
  "header",
  "section",
  "nav",
  "data",
  "footer",
  "time",
  "svg",
  "g",
  "h",
  "path",
  "canvas",
  "fblike",
  "sfmsg",
  "video",
  "source",
  "glightbox",
  "bdi",
  "hgroup",  
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
  spec.taskno = spec.fields.shift();
  spec.url = spec.fields.join(",");

  spec.server = spec.url.split(/\//).slice(0,3).join("/");					     
    
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

  at.procs.filter = new at.mods.eachline(function(line) {
    var that = this;

    if (line === '') { return; }
      
    var output = [];
    
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
      output.push([//"#",
			 "mkdir"
			 , "-p", ["experiments","data",download.localdir].join("/")
			 , "\n" ].join(" "));
      output.push(["test", "-f", ["experiments","data",download.localfile].join("/"), '||', "\n"].join(" "));
      output.push(["(", "\n"].join(" "));     
      at.rez.archives.forEach(function(arch) {
        output.push(["tar",
			   "xvzOf", arch,
			   ["req",download.localfile].join("/"),
			   "||",
			   "\n" ].join(" "));
      });
      output.push([//"#",
			 "curl"
			 //, "-s" 
			 //, "-o", download.localfile
			 , ["'",download.url,"'"].join("")
			 , "\n" ].join(" "));
      output.push([")", ">", ["experiments","data",download.localfile].join("/"), "\n"].join(" "));
    });
    output.push(["echo"
		       , ["'", line, "'"].join("")
		       , "\n" ].join(" "));

    return output.join("");
  });


  at.procs.swfetch = at.mods.cp.spawn('bash', ['-c',
                                               ["tee /tmp/swfetch.sh",
                                                "bash -"].join(" | ")]);

  at.procs.filter2 = new at.mods.eachline(function(line) {
    if (line === '') { return; }
        
    var that = this;

    var output = [];
      
    console.error("LINE2: %s", line);

    var spec = {};

    spec.line = line;
    spec.downloads = {};
    at.eq.emit('task', spec);    
      
    output.push(["xsltproc", require.resolve('./clear.xsl'), ["experiments","data",spec.downloads.dom.localfile].join("/") , '|', "java"
		       , "-jar", require.resolve("./smartwrap-cli.jar")
		       //, '-l', 'FINEST'
		       , "-e", ["experiments","data",spec.downloads.examples.localfile].join("/")
                 , "-d", "-",
		       , "-i", ["experiments","data",spec.downloads.meta.localfile].join("/")
		       , "--format", "xhtml"
		       , "-o", ["war", "atasks", [spec.taskid, '.xhtml'].join("")].join("/")
		       , "2>", ["experiments","data",[spec.taskid, '.err'].join("")].join("/")
		       , "\n"].join(" "));
    output.push(["echo"
		       , ["war", "atasks", [spec.taskid, '.xhtml'].join("")].join("/")
		       , "\n"].join(" "));
    output.push(["echo"
		       , spec.url
		       , "\n"].join(" "));
    output.push(["echo"
		       , spec.server
		       , "\n"].join(" "));

    return output.join("");
  });

  at.procs.exec2 = at.mods.cp.spawn('bash', ['-c',
                                             ["tee /tmp/swwrap.sh",
                                              "bash -",
					      "tee /tmp/swtofix.txt",
					     ].join(" | ")]);

  var perl = "s/\\&\\#195;\\&\\#8218;\\&\\#194;//g; s/[\\x{0c3}\\x{0c2}]./ /g;";
  console.error("PERL %s", perl);
  
  at.procs.fix = at.mods.cp.spawn('parallel',
				  ['-n3',
				   ["tidy", "-utf8", "-quiet", "--show-warnings", "0", 
				    "--new-blocklevel-tags", at.rez.extratags.join(","),
				    "-asxml", "-numeric", "{1}", 
                                    '2>>/tmp/tidy.err',
				    "|", "tee", "{1.}00.xhtml",
				    "|", "perl", "-pe", ['"',perl,'"'].join(""),
				    "|", "tee", "{1.}01.xhtml",
				    "|", "xsltproc",
				      '--stringparam', 'resolveServer', "{3}",
				      '--stringparam', 'resolveBase', "{2}",				    
				    require.resolve("./general.xsl"), "-",
				    "|", "xmllint", "--format", "--nsclean", "-",
				    "|", "tee", "{1.}0.xhtml",
				    "|", "tee", "{1.}0.html",
				    "|", "xsltproc", require.resolve("./underuse.xsl"), "-",
				    "|", "xmllint", "--format", "--nsclean", "-",
				    "|", "perl", "-pe", "'s@html:@@g;'",
				    "|", "tee", "{1.}1.xhtml",
				    "|", "tee", "{1.}1.html",
				    "|", "xsltproc", require.resolve("./overuse.xsl"), "-",
				    "|", "xmllint", "--format", "--nsclean", "-",
				    "|", "perl", "-pe", "'s@html:@@g;'",
				    "|", "tee", "{1.}3.xhtml",
				    "|", "tee", "{1.}3.html",
				    "> /dev/null",
				   ].join(" ")]);
  
  if (at.procs.curltee) {
    at.procs.curl.stdout.pipe(at.procs.curltee.stdin);
    at.procs.curltee.stdout.pipe(at.procs.filter);
  } else {
    at.procs.curl.stdout.pipe(at.procs.filter.stdin);
  }
  at.procs.curl.stderr.pipe(process.stderr);

  if (at.procs.scrtee) {
    at.procs.filter.pipe(at.procs.scrtee.stdin);
    at.procs.scrtee.stdout.pipe(at.procs.swfetch.stdin);
  } else {
    at.procs.filter.pipe(at.procs.swfetch.stdin);
  }

  at.procs.swfetch.stdout.pipe(at.procs.filter2);
  at.procs.swfetch.stderr.pipe(process.stderr);

  at.procs.filter2.pipe(at.procs.exec2.stdin);

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

  at.procs.ffilt = new at.mods.eachline(function(line) {
    at.rez.archives.push(line);  
  });

  at.procs.find.stdout.pipe(at.procs.ffilt).pipe(process.stdout);
  at.procs.find.stderr.pipe(process.stderr);
    
  at.procs.find.stdout.on('end', function() {
    at.procs = {};
    console.error("ARCHIVES %j", at.rez.archives);

    at.eq.emit('tasklist');
  });
});

at.eq.emit('archives', {root:'./archive'});
