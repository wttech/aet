<template>
  <div id="app" class="app-container wrapper">
    <ReportOptions :options="options"/>
    <div class="header container">
      <img id="logo" class="logo" src="./assets/logo.png">
      <h3>
        <span>Company:</span>{{suite.company}}
        <span>Project:</span>{{suite.project}}
        <span>Name:</span>{{suite.name}}
        <span>CorrelationID:</span>{{suite.correlationId}}
      </h3>
    </div>
    <div class="suite">
      <div class="test container" v-for="test in suite.tests">
        <h4 class="testName"> TEST: {{test.name}} </h4>
        <div class="url" v-for="url in test.urls">
          <p class="domain">Domain: <span>{{url.domain}}</span></p>
          <p class="urlname">Url name: <span>{{url.name}}</span></p>
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
  body {
    margin: 0;
    padding: 0;
    background-color: #fff;
  }

  .wrapper {
    width: 960px;
    margin: auto;
  }

  .app-container {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
    border-left: 1px solid #ddd;
    border-right: 1px solid #ddd;
  }

  @media (max-width: 959px) {
    .wrapper {
      width: 100%;
    }
  }

  @media print {
    .wrapper {
      width: 100%;
    }
  }

  .container {
    padding: 0 15px;
    box-sizing: border-box;
    border-bottom: 1px solid #2c3e50;
  }

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 20px;
    padding-bottom: 20px;
    margin-top: 43px;;
    background-color: #efefef;
  }

  .header h3 {
    margin: 0;
    font-weight: bold;
  }

  .header h3 span {
    margin-right: 5px;
    margin-left: 10px;
    font-weight: normal;
  }

  .header h3 span:first-child {
    margin-left: 0;
  }

  .logo {
    height: 40px;
    margin-right: 20px;
  }

  .test {
    padding-top: 20px;
    padding-bottom: 20px;
  }

  .testName {
    margin-top: 0;
  }

  .domain span, .urlname span {
    color: #0097fe;
  }

  .grey {
    color: #85898e;
    margin-bottom: 5px;
    font-size: 14px;
  }
</style>
