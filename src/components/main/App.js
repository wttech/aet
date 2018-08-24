import React from 'react';
import { BrowserRouter, Route, Switch  } from 'react-router-dom';
import SuiteGenerator from "./SuiteGenerator";
import "../../assets/sass/main.scss";
import "../../assets/icons/fontawesome-all.min";

const App = () => (
    <div>
        <SuiteGenerator />
    </div>
);


export default App;