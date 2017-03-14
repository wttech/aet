<template>
  <div v-if="items != [] && items != undefined">
    <p>Source W3C-HTML5: <span v-if="items.length == 0">success</span></p>
    <ul class="sourceHTML5">
      <li v-for="item in items">
        <span>Line: {{item.line}}, column: {{item.column}}</span>
        <p v-html="item.message"></p>
      </li>
    </ul>
  </div>
</template>
<script>
  export default {
    name: 'HTML5ResultComponent',

    props: {
      model: Object,
      options: Object

    },
    methods: {
      getResultArtifactUrl: function () {
        if (this.model != null && this.model.stepResult != null) {
          var id = this.model.stepResult.artifactId;
          return this.getArtifactUrl(id);
        }
        return null;
      },

      getSource: function () {
        var apiURL = this.getResultArtifactUrl();
        var self = this;
        $.get(apiURL, function( data ) {
          self.items = data.issues;
        });
      }
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

      this.getSource();
    }

  };
</script>
<style scoped>
  .sourceHTML5 span {
    font-size: 12px;
  }
</style>
