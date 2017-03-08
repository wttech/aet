// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import App from './App';
import VueResource from 'vue-resource';

Vue.prototype.getArtifactUrl = function (id) {

  // TODO after deploy to server change url and base to those commented out
  //        var url = location.href;
  //        var base = location.origin;
  let url = "https://karaf-staging-aet.cognifide.com/report/report.html?company=aet&project=aet&correlationId=aet-aet-main-1488953880865#/suite";
  let base = "https://karaf-staging-aet.cognifide.com";
  let suiteID = url.substring(url.indexOf("?"), url.indexOf("#"));

  return base + "/api/artifact" + suiteID + "&id=" + id;
};

Vue.use(VueResource);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  components: {App},
});
