<template>
  <div class="AetScreenComparison">DIFF:<br>
    <img :src="getResultArtifactUrl()">
  </div>
</template>

<script>
  export default {

    name: 'AetScreenComparison',
    props: {
      model: Object
    },

    methods: {
      getResultArtifactUrl: function () {
        if (this.model != null && this.model.stepResult != null) {
          var id = this.model.stepResult.artifactId;
          return this.getArtifactUrl(id);
        }
        return null;
      }
    }
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

  .ScreenComparison {
    overflow: hidden;
  }

  img {
    width: 100px;
  }
</style>
