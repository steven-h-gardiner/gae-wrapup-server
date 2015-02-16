var luigi = {};

luigi.api = {};

luigi.mods = {};
luigi.mods.cp = require('child_process');
luigi.mods.ee = require('events').EventEmitter;

luigi.ee = new luigi.mods.ee();

luigi.foo = 'bar';

luigi.urfilter = {};

luigi.ee.on('register', function(spec) {
  if ((spec.eventName === 'line') || (spec.eventName === 'para')) {
    //console.error("REG: %j", {});

    var that = spec.referent;
    
    var dataListener = that.dataListener;
    if (! dataListener) {
      that.dataListener = function(d) {      
        buffer += d.toString();
        while (true) {
          var pivot = buffer.indexOf("\n");
          if (pivot < 0) { 
            that.ee.emit('para');          
            return; 
          }
          var line = buffer.slice(0,pivot);
          buffer = buffer.slice(1+pivot);
          
          that.ee.emit('line', line);
        }
      };
    
      var buffer = '';
      that.input.on('data', that.dataListener);

      that.input.on('end', function() {
        //console.log("HUP");
        that.stdin.end();
        that.output.end();
      });    
    }
  }
  if (spec.eventName === 'line') {
    that.ee.on('line', function(line) {
      spec.callback.call(that, line);
    });
  }
  if (spec.eventName === 'para') {
    that.ee.on('para', function() {
      spec.callback.call(that);
    });
  }  
});

luigi.urfilter.on = function(eventName, callback) {
  if (eventName === 'line') {
    var spec = {};
    spec.eventName = eventName;
    spec.callback = callback;
    spec.referent = this;
    luigi.ee.emit('register', spec);
  }
  if (eventName === 'para') {
    var spec = {};
    spec.eventName = eventName;
    spec.callback = callback;
    spec.referent = this;
    luigi.ee.emit('register', spec);
  }
};

luigi.api.filter = function() {
  var that = Object.create(luigi.urfilter);

  that.ee = new luigi.mods.ee();

  that.procs = {};

  that.procs.input = luigi.mods.cp.spawn('cat', []);
  that.procs.output = luigi.mods.cp.spawn('cat', []);

  that.stdin = that.procs.input.stdin;
  that.stdout = that.procs.output.stdout;
  that.input = that.procs.input.stdout;
  that.output = that.procs.output.stdin;

  return that;
};

luigi.urconfluence = {};

luigi.urconfluence.on = function(eventName, callback) {
  this.ee.on(eventName, callback);
};

luigi.api.confluence = function(streams) {
  var that = Object.create(luigi.urconfluence);

  that.ee = new luigi.mods.ee();

  var checklist = [];
  streams.forEach(function(strm) {
    checklist.push(strm);
    strm.on('end', function() {
      checklist.pop();
      if (checklist.length == 0) {
        console.error("GULP");
        that.ee.emit('end');
      }
    });
  });

  return that;
};

module.exports = luigi.api;
