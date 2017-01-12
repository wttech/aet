// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import App from './App';

Vue.prototype.getArtifactUrl = function (id) {
//        var url = location.href;
//        var base = location.origin;
  var url = "https://karaf-integration-aet.cognifide.com/report/report.html?company=aet&project=aet&correlationId=aet-aet-main-1484202251849#/suite" //mock
  var base = "https://karaf-integration-aet.cognifide.com";
  var suiteID = url.substring(url.indexOf("?"), url.indexOf("#"));

  return base + "/api/artifact" + suiteID + "&id=" + id;
};

var VueResource = require('vue-resource');
Vue.use(VueResource);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  components: {App},
});
