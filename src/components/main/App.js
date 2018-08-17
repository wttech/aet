import React from 'react';
import { BrowserRouter, Route, Switch  } from 'react-router-dom';
import SuiteGenerator from "./SuiteGenerator";
import "../../assets/sass/main.scss";
import "../../assets/icons/fontawesome-all.min";

const App = () => (
    <div>
        <BrowserRouter>
            <Switch>
                <Route exact path="/" component={SuiteGenerator}/>
            </Switch>
        </BrowserRouter>
    </div>
);


export default App;