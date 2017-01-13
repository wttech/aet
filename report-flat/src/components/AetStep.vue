<template>
  <div v-if="showStep">
    {{model.type}}|{{model.name}} <span v-if="model.stepResult">{{model.stepResult.status}}</span>
    <ScreenComponent :options="options" :model="model" v-if="model.type=='screen' && options.showScreen"/>
    <JsErrorsComponent :options="options" :model="model" v-if="model.type=='js-errors'  && options.showJS"/>
  </div>
</template>

<script>

  import ScreenComponent from './screen/ScreenComponent';
  import JsErrorsComponent from './jserrors/JsErrorsComponent';

  export default {
    components: {
      ScreenComponent, JsErrorsComponent
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
