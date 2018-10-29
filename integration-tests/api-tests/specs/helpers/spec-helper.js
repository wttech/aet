function MainHelper() {

    this.recursiveKeySearch = (key, data) => {
        let results = [];
        if (data.constructor === Array) {
            for (let i = 0, len = data.length; i < len; i++) {
                results = results.concat(this.recursiveKeySearch(key, data[i]));
            }
        }
        else if ((data !== null) && (typeof(data) == "object")) {
            for (let dataKey in data) {
                if (key === dataKey) {
                    results.push(data[key]);
                }
                results = results.concat(this.recursiveKeySearch(key, data[dataKey]));
            }
        }
        return results;
    };
}

module.exports = new MainHelper();

