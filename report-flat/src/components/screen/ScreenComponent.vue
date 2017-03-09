<template>
  <div class="AetScreenStepContainer">
    <div v-if="options.showPattern" class="imgPattern">
      <p class="grey">Pattern:</p>
      <img :src="getPatternArtifactUrl()">
    </div>
    <div v-if="options.showCollected" class="imgCollected">
      <p class="grey">Collected:</p>
      <img v-if="model.stepResult.status=='COLLECTED'" :src="getCollectedArtifactUrl()">
      <span v-if="!model.stepResult.status=='COLLECTED'"> {{model.stepResult.status}}</span>
    </div>

    <div v-if="model.stepResult.status=='COLLECTED' && options.showMask" v-for="step in model.comparators">
      <ScreenComparison :model="step"/>
    </div>
  </div>
</template>

<script>
  import ScreenComparison from './ScreenComparisonResult';
  export default {
    name: 'Screen',
    components: {
      ScreenComparison,
    },
    props: {
      model: Object,
      options: Object
    },

    methods: {
      getPatternArtifactUrl: function () {
        if (this.model.pattern != null) {
          return this.getArtifactUrl(this.model.pattern);
        }
        return null;
      },

      getCollectedArtifactUrl: function () {
        if (this.model.stepResult != null && this.model.stepResult.artifactId != null) {
          return this.getArtifactUrl(this.model.stepResult.artifactId);
        }
        return null;
      }
    }
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .imgPattern, .imgCollected {
    float: left;
    width: 33%;
    box-sizing: border-box;
    padding-right: 10px;
  }

  img {
    width: 100%
  }

  .AetScreenStepContainer {
    clear: right;
    overflow: hidden; /* will contain if #first is longer than #second */
  }


</style>
