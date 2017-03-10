<template>
  <div>
    <p>Status codes:</p>
    <ol class="statusCodes">
      <li v-for="item in items">
        <span>{{ item.code }}: </span> {{ item.url }}
      </li>
    </ol>
  </div>
</template>
<script>
  export default {
    name: 'StatusCodesResultComponent',

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

      getStatusCodes: function () {
        var apiURL = this.getResultArtifactUrl();
        var self = this;
        $.get(apiURL, function( data ) {
          self.items = data.statusCodes;
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
      this.getStatusCodes();
    }

  };
</script>
<style scoped>
  .statusCodes li span {
    font-weight: bold;
  }
</style>
