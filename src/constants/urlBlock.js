const urlBlock = {
  deps: null,
  dropTo: "URLs",
  group: "URLs",
  parameters: {
    name: {
      current: null,
      default: null,
      isMandatory: false,
      description: "URL's name for the report app",
      name: "Name",
      tag: "name",
      values: null,
    },
    href: {
      current: null,
      default: null,
      isMandatory: true,
      description: "URL to be tested",
      name: "URL",
      tag: "href",
      values: null,
    }
  },
  proxy: false,
  tag: "url",
  type: "URL",
  wiki: "http://www.google.com",
};

export default urlBlock;