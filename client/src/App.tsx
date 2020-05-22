import React from 'react';
import logo from './logo.svg';
import { Counter } from './features/counter/Counter';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link,
  useRouteMatch,
  useParams,
  useLocation
} from "react-router-dom";
import './App.css';

/*
function App() {
  return (
<div>
    <div className="nav-container">
        <div>
            <div className="bar bar--sm visible-xs">
                <div className="container">
                    <div className="row">
                        <div className="col-3 col-md-2">
                            <a href="index.html"><img className="logo logo-dark" alt="logo" src="img/logo-dark.png" /><img className="logo logo-light" alt="logo" src="img/logo-light.png" /></a>
                        </div>
                        <div className="col-9 col-md-10 text-right">
                            <a href="#" className="hamburger-toggle" data-toggle-className="#menu1;hidden-xs hidden-sm"><i className="icon icon--sm stack-interface stack-menu"></i></a>
                        </div>
                    </div>
                </div>
            </div>
            <nav id="menu1" className="bar bar-1 hidden-xs">
                <div className="container">
                    <div className="row">
                        <div className="col-lg-1 hidden-xs col-md-3">
                            <div className="bar__module">
                                <a href="index.html"><img className="logo logo-dark" alt="logo" src="img/logo4.png" /><img className="logo logo-light" alt="logo" src="img/logo-light.png" /></a>
                            </div>
                        </div>
                        <div className="col-lg-11 col-md-12 text-right text-left-xs text-left-sm">
                            <div className="bar__module">
                                <a className="btn btn--sm type--uppercase" href="#customise-template"><span className="btn__text">Manage my mocks<br /></span></a>
                                <a className="btn btn--sm btn--primary type--uppercase" href="#purchase-template"><span className="btn__text">NEW MOCK</span></a>
                            </div>
                        </div>
                    </div>
                </div>
            </nav>
        </div>
    </div>

    <div className="main-container">
        <a id="mocky-headtitle" className="in-page-link"></a>
        <section className="text-center space--xs">
            <div className="container">
                <div className="row">
                    <div className="col-md-10 col-lg-8">
                        <h1>Mocky</h1>
                        <p className="lead"> The world easiest &amp; fastest tool belts to mock your APIs.<br /></p>
                    </div>
                </div>
            </div>
        </section>

        <section className="space--xxs">
          <div className="container">
              <div className="App">
                  <img src={logo} className="App-logo" alt="logo" />
                  <Counter />
                  <p>
                    Edit <code>src/App.tsx</code> and save to reload.
                  </p>
                  <span>
                    <span>Learn </span>
                    <a
                      className="App-link"
                      href="https://reactjs.org/"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      React
                    </a>
                    <span>, </span>
                    <a
                      className="App-link"
                      href="https://redux.js.org/"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      Redux
                    </a>
                    <span>, </span>
                    <a
                      className="App-link"
                      href="https://redux-toolkit.js.org/"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      Redux Toolkit
                    </a>
                    ,<span> and </span>
                    <a
                      className="App-link"
                      href="https://react-redux.js.org/"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      React Redux
                    </a>
                  </span>
              </div>
          </div>
        </section>

        <a id="what-is-mocky" className="in-page-link"></a>
        <section className="switchable feature-large space--xxs">
            <div className="container">
                <div className="row justify-content-around">
                    <div className="col-md-6"><img alt="Image Thumbnail" className="border--round box-shadow-wide" src="img/mocky-code3.png" /></div>
                    <div className="col-md-6 col-lg-5">
                        <div className="switchable__text">
                            <h2>API Mocks for Free</h2>
                            <p className="lead">Don't wait for the backend to be ready, generate custom API responses with Mocky and start working on your application straightaway<br /></p>
                            <a href="#">Start designing your mock »</a>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section className="imagebg space--xxs" data-gradient-bg="#4876BD,#5448BD,#8F48BD,#BD48B1">
            <div className="container">
                <div className="row">
                    <div className="col-md-12">
                        <div className="cta cta-1 cta--horizontal boxed boxed--border text-center-xs">
                            <div className="row justify-content-center p-5">
                                <div className="col-lg-3">
                                    <h4>No signup</h4>
                                </div>
                                <div className="col-lg-4">
                                    <p className="lead">Start designing your mock<br /></p>
                                </div>
                                <div className="col-lg-4 text-center">
                                    <a className="btn btn--primary type--uppercase" href="#"><span className="btn__text">NEW MOCK</span></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <a id="features" className="in-page-link"></a>
        <section className="space--xxs">
            <div className="container">
                <div className="row">
                    <div className="col-md-6 col-lg-3">
                        <div className="feature feature-6"><i className="icon color--primary icon-Money-2 icon--sm"></i>
                            <h5>Free &amp; Unlimited</h5>
                            <p>Mocky is free to use, no ads, no hidden subscriptions or service limits.&nbsp;Your mocks will be available <b>forever</b> (just call it one time per year), but without any <a href="http://www.toto.fr">guarantee</a>.<br /></p>
                        </div>
                    </div>
                    <div className="col-md-6 col-lg-3">
                        <div className="feature feature-6"><i className="icon color--primary icon-Box-Full icon--sm"></i>
                            <h5>Total control</h5>
                            <p>New in Mocky, you can now update or delete your mocks at any time.<br />The next release will go further with sign-in feature and cloud-based mock management</p>
                        </div>
                    </div>
                    <div className="col-md-6 col-lg-3">
                        <div className="feature feature-6"><i className="icon color--primary icon-Chemical icon--sm"></i>
                            <h5>Developper Friendly</h5>
                            <p>Mocky is compatible with JS, Mobile and Server applications, featuring CORS, JSONP and GZIP responses.<br />No authentication, just call it!</p>
                        </div>
                    </div>
                    <div className="col-md-6 col-lg-3">
                        <div className="feature feature-6"><i className="icon color--primary icon-Code-Window icon--sm"></i>
                            <h5>Open Source&nbsp;</h5>
                            <p> Mocky is distributed with Apache 2 licence on Github. Ready-to-use distributions are available to host your own Mocky instance.</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <footer className="text-center-xs space--xs">
            <div className="container">
                <div className="row">
                    <div className="col-sm-7">
                        <ul className="list-inline">
                            <li><a href="About" target="_self"><span className="h6 type--uppercase">About</span></a></li>
                            <li><a href="#"><span className="h6 type--uppercase"><p>FAQ</p></span></a></li>
                            <li><a href="#"><span className="h6 type--uppercase"><p>You like it? Buy me a beer!</p></span></a></li>
                        </ul>
                    </div>
                    <div className="col-sm-5 text-right text-center-xs">
                        <ul className="social-list list-inline list--hover">
                            <li><a href="#"><i className="socicon socicon-twitter icon icon--xs"></i></a></li>
                        </ul>
                    </div>
                </div>
                <div className="row">
                    <div className="col-sm-7"><span className="type--fine-print">© <span className="update-year">2020</span>&nbsp;Mocky.io</span><a className="type--fine-print" href="#">Privacy Policy</a>
                        <a className="type--fine-print" href="#"></a>
                    </div>
                    <div className="col-sm-5 text-right text-center-xs">
                        <a className="type--fine-print" href="#"></a>
                    </div>
                </div>
            </div>
        </footer>
    </div>
</div>                        
                        
  );
}

export default App;
*/

export default function BasicExample() {
  return (
    <Router>
      <div>
        <ul>
          <li>
            <Link to="/">Home</Link>
          </li>
          <li>
            <Link to="/about">About</Link>
          </li>
          <li>
            <Link to="/dashboard">Dashboard</Link>
          </li>
          <li>
            <Link to="/topics">Topics</Link>
          </li>
        </ul>

        <hr />

        <Switch>
          <Route exact path="/">
            <Home />
          </Route>
          <Route path="/about">
            <About />
          </Route>
          <Route path="/dashboard">
            <Dashboard />
          </Route>
          <Route path="/topics">
            <Topics />
          </Route>
          <Route path="*">
            <NoMatch />
          </Route>
        </Switch>
      </div>
    </Router>
  );
}

// You can think of these components as "pages"
// in your app.

function Home() {
  return (
    <div>
      <h2>Home</h2>
    </div>
  );
}

function About() {
  return (
    <div>
      <h2>About</h2>
    </div>
  );
}

function Dashboard() {
  return (
    <div>
      <h2>Dashboard</h2>
    </div>
  );
}

function Topics() {
  let match = useRouteMatch();

  return (
    <div>
      <h2>Topics</h2>

      <ul>
        <li>
          <Link to={`${match.url}/components`}>Components</Link>
        </li>
        <li>
          <Link to={`${match.url}/props-v-state`}>
            Props v. State
          </Link>
        </li>
      </ul>

      <Switch>
        <Route path={`${match.path}/:topicId`}>
          <Topic />
        </Route>
        <Route path={match.path}>
          <h3>Please select a topic.</h3>
        </Route>
      </Switch>
    </div>
  );
}

function Topic() {
  let { topicId } = useParams();
  return <h3>Requested topic ID: {topicId}</h3>;
}

function NoMatch() {
  let location = useLocation();

  return (
    <div>
      <h3>
        No match for <code>{location.pathname}</code>
      </h3>
    </div>
  );
}