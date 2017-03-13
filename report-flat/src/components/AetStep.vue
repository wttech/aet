<template>
  <div v-if="showStep">
    <p> {{model.type}} | {{model.name}}</p>
    <p v-if="model.stepResult">Status: <b>{{model.stepResult.status}}</b></p>
    <ScreenComponent :options="options" :model="model" v-if="model.type=='screen' && options.showScreen"/>
    <JsErrorsComponent :options="options" :model="model" v-if="model.type=='js-errors'  && options.showJS"/>
    <StatusCodesComponent :options="options" :model="model" v-if="model.type=='status-codes' && options.showStatusCodes"/>
    <!--condition for comparator='w3c-html5 must be improved!-->
    <HTML5Component :options="options" :model="model" v-if="model.comparators[0].parameters.comparator=='w3c-html5' && options.showHTML5"/>
  </div>
</template>

<script>

  import ScreenComponent from './screen/ScreenComponent';
  import JsErrorsComponent from './jserrors/JsErrorsComponent';
  import StatusCodesComponent from './statuscodes/StatusCodesComponent';
  import HTML5Component from './html5/HTML5Component';

  export default {
    components: {
      ScreenComponent, JsErrorsComponent, StatusCodesComponent, HTML5Component
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
