<template>
  <div id="app">
    <ReportOptions :options="options"/>
    <div class="suite">
      <img id="logo" src="./assets/logo.png">
      Company: {{suite.company}} Project: {{suite.project}} Name: {{suite.name}} CorrelationID:
      {{suite.correlationId}}
      <br><br>
      <div class="test" v-for="test in suite.tests">
        <span class="testName"/> TEST: {{test.name}}
        <div class="url" v-for="url in test.urls">
          <span class="domain">{{url.domain}}</span><span class="urlname">{{url.name}}</span>
          <br>
          <div class="step" v-for="step in url.steps">
            <AetStep :options="options" :model="step"></AetStep>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import AetStep from './components/AetStep';
  import ReportOptions from './components/ReportOptions';
  import Mock from './Mock';

  export default {
    name: 'app',
    components: {
      AetStep, ReportOptions
    },

    data() {
      return {
        suite: Mock,
        options: {
          showPattern: true,
          showCollected: true,
          showMask: true,
        },
      }
        ;
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
