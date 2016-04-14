var st = {};

st.mods = {};
st.mods.cp = require('child_process');
st.mods.url = require('url');
st.mods.eachline = require('eachline');

st.rez = {};

st.rez.wrapup = {};
st.rez.wrapup.prefix = "https://gae-wrapup-server.appspot.com";
st.rez.wrapup.path = "/access/tasklist.csv";

st.rez.swauth = {};
st.rez.swauth.prefix = "https://sw-auth.appspot.com";
st.rez.swauth.path = "/admin/blob";
st.rez.swauth.token = "abcd1253";

st.rez.extratags = [
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
  "figure",
  "symbol",
  "aside",
];

st.procs = {};

process.on('clear', function(cSpec) {
  var oSpec = Object.create(cSpec);
  st.procs.clear = st.mods.cp.spawn('bash', ['-c',
					     ['ls experiments/data/*/document.xhtml',
					      'parallel -n1 echo {} \\; echo {.}1.xhtml',
					      ['parallel -n2 --bar test -f {2} \\|\\| xsltproc',
					       require.resolve('./clear.xsl'),
					       '{1} \\> {2}',
					       ].join(" "),
					      ].join(" | ")]);
  
  st.procs.clear.stdout.pipe(process.stdout);
  st.procs.clear.stderr.pipe(process.stderr);

  st.procs.clear.stdout.on('end', function() {
    process.emit('genrez', oSpec);
  });
});

process.on('genrez', function(cSpec) {
  var oSpec = Object.create(cSpec);
  st.procs.rez = st.mods.cp.spawn('bash', ['-c',
					     ['sqlite3 -line study.sqlite',
					      'cut -c10-',
					      ['parallel -n4 xsltproc',
					       '--stringparam', 'resolveBase', '{2}',
					       '--stringparam', 'taskid', '{3}',
					       require.resolve('./rez.xsl'),
					       'experiments/data/{1}/document1.xhtml',	
					       ].join(" "),
					      'parallel -n4 echo {4},,,{3},,,{2},,,{1}',
					      'tee rez.txt',
					      ].join(" | ")]);

  st.procs.rezfilt = st.mods.eachline(function(line) {
    if (line === '') { return; }

    var lspec = {};
    lspec.fields = line.split(/,,,/);
    lspec.taskid = lspec.fields.shift();
    lspec.localfile = lspec.fields.shift() + ".css";
    lspec.relative = lspec.fields.shift();
    lspec.baseurl = lspec.fields.join(',,,');
    delete lspec.fields;
    

    lspec.relative1 = lspec.relative.replace(/\%20/g, '');
    lspec.resolved = st.mods.url.resolve(lspec.baseurl, lspec.relative1);
    
    console.error("LSPEC: %s", JSON.stringify(lspec, null, 2));
    
    return [lspec.taskid, lspec.relative, lspec.resolved, lspec.localfile, ""].join("\n");
  });

  st.procs.rezwrit = st.mods.cp.spawn('bash', ['-c',
					       ['tee /tmp/rez3.txt',
					        ['parallel -n4',
						 ['echo /resources/resource',
						  'echo /resources/resource/@taskid={1}',
						  'echo /resources/resource/@relpath={2}',
						  'echo /resources/resource/@abspath={3}',
						  'echo /resources/resource/@locpath={4}',
						  ].join(" \\; "),
						 ].join(" "),
						'2xml',
						'xmllint --format -',
						'tee rez.xml',
						//'cat > /dev/null',
						].join(" | ")]);
  
  st.procs.rezfetch = st.mods.cp.spawn('bash', ['-c',
						[['xmlstarlet sel -T',
						  '-t -m /resources/resource',
						  '-v @abspath -nl',
						  '-v @taskid -nl',
						  '-v @locpath -nl',
						  ].join(" "),
						 'tee /tmp/rezfetch.txt',
						 'parallel --bar -n3 mkdir -p war/atasks/{2} \\; test -f war/atasks/{2}/{3} \\|\\| curl --compressed -s -L {1} \\> war/atasks/{2}/{3}'
						 ].join(' | ')]);
  st.procs.rez.stdout.pipe(st.procs.rezfilt).pipe(st.procs.rezwrit.stdin);
  st.procs.rezfetch.stdout.pipe(process.stdout);
  st.procs.rezwrit.stdout.pipe(st.procs.rezfetch.stdin);
  st.procs.rez.stderr.pipe(process.stderr);
  st.procs.rezfetch.stderr.pipe(process.stderr);
  st.procs.rezwrit.stderr.pipe(process.stderr);

  st.procs.rez.stdin.write(['select',
			    ['representative as wrapid',
			     "url as basurl",
			     'taskid as taskid',
			     ].join(" , "),
			    'from studytasks ; \n'
			    ].join(" "));
  st.procs.rez.stdin.end();
  
  
  st.procs.rezfetch.stdout.on('end', function() {
    process.emit('wrap', oSpec);
  });
});

process.on('wrap', function(wSpec) {
  var oSpec = Object.create(wSpec);
  st.procs.wrap = st.mods.cp.spawn('bash', ['-c',
					     ['ls experiments/data/*/document1.xhtml',
					      'parallel -n1 dirname {}',
					      'parallel -n1 echo {}/examples.json \\; echo {}/document1.xhtml \\; echo {}/meta.json \\; echo {}/wrapped.xhtml \\; echo {}/wrap.err',
					      'tee /tmp/wrapargs.txt',
					      ['parallel -n5 --bar',
					       'test -f {4} \\|\\| java -jar',
					       require.resolve('./smartwrap-cli.jar'),
					       '-e {1} -d {2} -i {3} --format xhtml -o {4} 2\\> {5}',
					       ].join(" "),
					      ].join(" | ")]);
  
  st.procs.wrap.stdout.pipe(process.stdout);
  st.procs.wrap.stderr.pipe(process.stderr);

  st.procs.wrap.stdout.on('end', function() {
    process.emit('fix', oSpec);
  });
});

process.on('fix', function(fSpec) {
  var oSpec = Object.create(fSpec);
  oSpec.perl = "s/\\&\\#195;\\&\\#8218;\\&\\#194;//g; s/[\\x{0c3}\\x{0c2}]./ /g;";
  st.procs.fixsel = st.mods.cp.spawn('bash', ['-c',
					      ['sqlite3 -line study.sqlite',
					       'cut -c10-',
					       'tee /tmp/fixsel.txt',
					       ].join(" | ")]);
  st.procs.fix = st.mods.cp.spawn('parallel', ['-n3',
					       ['cat experiments/data/{2}/wrapped.xhtml',
						'tee war/atasks/{1}.xhtml',
						['tidy -utf8 -quiet --show-warnings 0 -asxml -numeric',
						 '--new-blocklevel-tags',st.rez.extratags.join(","),
						 '- 2>>/tmp/tidy.err',
						 ].join(" "),
						'tee war/atasks/{1}00.xhtml',
						['perl -pe "', oSpec.perl, '"'].join(""),
						'tee war/atasks/{1}01.xhtml',
						['xsltproc',
						 '--stringparam', 'rezurl', "/home/sopolcho/steve/work/gae-wrapup-server/rez.xml",
						 '--stringparam', 'taskid', "{1}",
						 require.resolve('./general.xsl'),
						 '-',
						 ].join(" "),
						'tee war/atasks/{1}0.xhtml',
						['xsltproc',
						 require.resolve('./underuse.xsl'),
						 '-',
						 ].join(" "),
						'tee war/atasks/{1}1.xhtml',
						['xsltproc',
						 require.resolve('./overuse.xsl'),
						 '-',
						 ].join(" "),
						'tee war/atasks/{1}3.xhtml',
						'cat > /dev/null',
						].join(" | ")]);
  
  st.procs.fixsel.stdout.pipe(st.procs.fix.stdin);
  st.procs.fix.stdout.pipe(process.stdout);
  st.procs.fixsel.stderr.pipe(process.stderr);
  st.procs.fix.stderr.pipe(process.stderr);

  st.procs.fixsel.stdin.write(['select',
			    ['taskid as taskid',
			     'representative as reqid',
			     ].join(" , "),
			    'from studytasks ; \n'
			    ].join(" "));
  st.procs.fixsel.stdin.end();
});

process.on('fetch', function(fSpec) {
  var oSpec = Object.create(fSpec);

  st.procs.sel = st.mods.cp.spawn('bash', ['-c',
					   ['tee /tmp/studyfetch.sql',
					    'sqlite3 -line study.sqlite',
					    'tee /tmp/studyfetch.out',
					    'cut -c10-',
					    ['parallel -n5',					     
					     ['mkdir -p experiments/data/{1}',
					      'echo experiments/data/{1}/examples.json',
					      'echo {2}',
					      'echo experiments/data/{1}/document.xhtml',
					      'echo {3}',
					      'echo experiments/data/{1}/meta.json',
					      'echo {4}',
					      ].join(" \\; "),
					     ].join(" "),
					    'parallel --bar -n 2 test -f {1} \\|\\| curl -s "{2}" \\> {1}',
					    ].join(" | ")]);

  st.procs.sel.stdout.pipe(process.stdout);
  st.procs.sel.stderr.pipe(process.stderr);

  oSpec.url = [st.rez.swauth.prefix,
	       st.rez.swauth.path,
	       '?token=',
	       st.rez.swauth.token,	       
	       ].join("");
  
  st.procs.sel.stdin.write(['select',
			    ['representative as dirnam',
			     ["'",
			      [oSpec.url,'type=examples','reqid='].join("&"),
			      "' || representative as exurl"].join(''),
			     ["'",
			      [oSpec.url,'type=document','reqid='].join("&"),
			      "' || representative as docurl"].join(''),
			     ["'",
			      [oSpec.url,'type=metainfo','reqid='].join("&"),
			      "' || representative as meturl"].join(''),
			     ].join(" , "),
			    'from studytasks ; \n'
			    ].join(" "));
  st.procs.sel.stdin.end();

  st.procs.sel.stdout.on('end', function() {
    process.emit('clear', oSpec);
  });
});

process.on('fetch0', function(fSpec) {
  var oSpec = Object.create(fSpec);

  st.procs.sel = st.mods.cp.spawn('bash', ['-c',
					   ['tee /tmp/studyfetch.sql',
					    'sqlite3 -line study.sqlite',
					    'tee /tmp/studyfetch.out',
					    'cut -c10-',
					    ['parallel -n3 --bar',
					     ['mkdir -p experiments/data/{1}',
					      ['curl \\\\"',
					       'https://sw-auth.appspot.com',
					       '/admin/blob',
					       '?token=',
					       'abcd1253',
					       '&type=examples&reqid={2}',
					       '\\\\" \\> experiments/data/{1}/examples.json',
					       ].join(""),
					      ].join(" \\; "),
					     ].join(" "),					   
					    ].join(" | ")]);

  st.procs.sel.stdout.pipe(process.stdout);
  st.procs.sel.stderr.pipe(process.stderr);

  st.procs.sel.stdin.write(['select',
			    ['taskid as dirnam',
			     'representative as reqid',
			     ].join(" , "),
			    'from studytasks ; \n'
			    ].join(" "));
  st.procs.sel.stdin.end();
  
});

process.on('tasklist', function(lSpec) {
  var oSpec = Object.create(lSpec);

  st.procs.curl = st.mods.cp.spawn('bash', ['-c',
					    [['( echo taskid,representative,taskno,url ;',
					      'curl -s ', st.rez.wrapup.prefix, st.rez.wrapup.path,
					      ' ; )',
					     ].join(""),
					     'tee study.csv',
					     'cat > /dev/null',
					     ].join(" | ")]);

  st.procs.load = st.mods.cp.spawn('bash', ['-c',
					    [['( cat - ; cat ', require.resolve('./study.sql'), ')'].join(" "),
					     'sqlite3 -csv study.sqlite',
					     'cat > /dev/null'
					    ].join(" | ")]);
  
  st.procs.curl.stdout.pipe(process.stdout);
  st.procs.curl.stderr.pipe(process.stderr);
  
  st.procs.load.stdout.pipe(process.stdout);
  st.procs.load.stderr.pipe(process.stderr);

  st.procs.load.stdin.write('drop table if exists study;\n');
  st.procs.curl.stdout.on('end', function() {
      st.procs.load.stdin.write(".import study.csv study\n");
      st.procs.load.stdin.end();
  });
      
  st.procs.load.stdout.on('end', function() {
      process.emit('fetch', oSpec);
  });
});

process.on('experiment', function(eSpec) {
  var oSpec = Object.create(eSpec);

  process.emit('tasklist', oSpec);
});

process.emit('experiment', {});
