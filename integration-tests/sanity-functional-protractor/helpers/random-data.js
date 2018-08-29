 'use strict';

 module.exports = (function pdjWait() {
     var api = {};

     api.getRandomNumber = () => {
         return Date.now() + Math.floor(Math.random() * 100000000);
     };

     api.getRandomEmail = (prefix = 'test', suffix = 'example.com') => {
         return prefix + api.getRandomNumber() + '@' + suffix;
     };

     return api;
 })();
