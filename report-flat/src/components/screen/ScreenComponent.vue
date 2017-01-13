<template>
  <div class="AetScreenStepContainer">
    <div v-if="options.showPattern" class="imgPattern">Pattern:<br>
      <img :src="getPatternArtifactUrl()">
    </div>
    <div v-if="options.showCollected" class="imgCollected">COLLECTED:<br>
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
  .imgPattern {
    float: left; /* add this */
  }

  .AetScreenStepContainer {
    clear: right;
    overflow: hidden; /* will contain if #first is longer than #second */
  }

  .imgCollected {
    float: left; /* add this */
    overflow: hidden;
  }

  img {
    width: 100px;
  }

</style>
