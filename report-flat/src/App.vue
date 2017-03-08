<template>
  <div id="app">
    <ReportOptions :options="options"/>
    <div class="suite">
      <hr/>
      <img id="logo" src="./assets/logo.png">
      Company: {{suite.company}} Project: {{suite.project}} Name: {{suite.name}} CorrelationID:
      {{suite.correlationId}}
      <hr/>
      <div class="test" v-for="test in suite.tests">
        <span class="testName"/> TEST: {{test.name}}
        <div class="url" v-for="url in test.urls">
          <span class="domain">{{url.domain}}</span>
          <span class="urlname">{{url.name}}</span>
          <br>
          <div class="step" v-for="step in url.steps">
            <AetStep :options="options" :model="step"></AetStep>
          </div>
        </div>
        <hr/>
      </div>
    </div>
  </div>
</template>

<script>
  import AetStep from './components/AetStep';
  import ReportOptions from './components/ReportOptions';

  export default {
    name: 'app',
    components: {
      AetStep, ReportOptions
    },

    data() {
      return {
        suite: Object,
        options: {
          showPattern: true,
          showCollected: true,
          showMask: true,
          showJS: true,
          showScreen: true
        },
      };
    },
    created: function () {
      // TODO change this URL  before deploying to server
      // ... when deployed,  url of this report will be in  form eg 'https://aet.cognifide.com/report-flat?company=aet&project=aet&correlationId=aet-aet-main-1484893457967'
      // so we need to get corelationId param from url and maeke a call for metadata.
      this.$http.get('https://karaf-staging-aet.cognifide.com/api/metadata?company=aet&project=aet&correlationId=aet-aet-main-1488969677485').then((response) => {
        this.suite = response.body;
      }, (response) => {
        alert("unable to get metadata report");
      });
    },
  };
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
    margin-top: 10px;
  }

  #logo {
    height: 20px;
  }
</style>
