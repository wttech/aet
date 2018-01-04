<template>
  <div>
    <span v-if="model.stepResult.status=='PASSED'" class="test-no-issues">
      No issues found
    </span>

    <div v-if="artifact != null && artifact.length > 0">
      <table class="table table-striped table-hover">
        <thead>
        <tr>
          <th>No.</th>
          <th>Error</th>
          <th>Source</th>
          <th>Line number</th>
        </tr>
        </thead>
        <tbody>

        <tr v-for="(item, index) in artifact">
          <td>{{index+1}}</td>
          <td>{{item.errorMessage}}</td>
          <td>
            <a :href="item.sourceName">{{item.sourceName}}</a>
          </td>
          <td>{{item.lineNumber}}</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-if="model.filters && model.filters.length > 0">
      <br>
      <span>
    JS Errors filters applied:
    </span>
      <table class="table table-striped table-hover">
        <thead>
        <tr>
          <th>No.</th>
          <th>Source</th>
          <th>Error</th>
          <th>Line number</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(filter, index) in model.filters">
          <td>{{index+1}}</td>
          <td>{{filter.parameters.source}}</td>
          <td v-if=="filter.parameters.error">{{filter.parameters.error}}</td>
          <td v-if="!filter.parameters.error">-</td>

          <td v-if="filter.parameters.line">{{filter.parameters.line}}</td>
          <td v-if="!filter.parameters.line">-</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>

  export default {
    name: 'JsErrorsResultComponent',

    props: {
      model: Object,
      options: Object,

    },
    data () {
      return {
        artifact: null,
        items: []
      }
    },

    created: function () {
      if (this.model.stepResult != null && this.model.stepResult.status != "PASSED") {
        var jsCompareArtifactUrl = this.getArtifactUrl(this.model.stepResult.artifactId);
        this.$http.get(jsCompareArtifactUrl).then((response) => {
          self.artifact = response.body;
        }, (response) => {
          alert("unable to get metadata report");
        });
      }
    }

  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .test-no-issues {
    display: block;
    font-family: "montserratlight", sans-serif;
    padding: 5px 0;
  }

  .table-striped > tbody > tr:nth-of-type(odd) {
    background-color: #f9f9f9;
  }

  .table-hover > tbody > tr:hover {
    background-color: #f5f5f5;
  }

  .table .table {
    background-color: #fff;
  }
</style>
