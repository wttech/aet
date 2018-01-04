<template>
  <div class="AetScreenComparison">
    <p class="grey">Diff:</p>
    <img :src="getResultArtifactUrl()" class="imgComparison">
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

  .imgComparison {
    float: left;
    width: 33%;
  }

  img {
    width: 100%;
  }
</style>
