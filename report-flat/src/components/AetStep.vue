<template>
  <div v-if="showStep">
    {{model.type}}|{{model.name}} <span v-if="model.stepResult">{{model.stepResult.status}}</span>
    <AetScreenStep :options="options" :model="model" v-if="model.type=='screen'"/>
  </div>
</template>

<script>

  import AetScreenStep from './screen/ScreenStep';

  export default {
    components: {
      AetScreenStep,
    },
    name: 'AetStep',
    props: {
      model: Object,
      options: Object

    },
    computed: {
      showStep: function () {
        return !(this.model.name == "open" || this.model.name == 'sleep' || this.model.name == 'click' || this.model.name == 'hide' || this.model.name == 'resolution')
      }
    },

  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
