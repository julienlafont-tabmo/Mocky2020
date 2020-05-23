import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Switch, Route, Link, useRouteMatch, useParams, useLocation } from 'react-router-dom';

import Loader from '../../components/Loader/Loader';
import ErrorBoundary from '../../components/ErrorBoundary/ErrorBoundary';
import NavBar from '../skeleton/NavBar';
import Footer from '../skeleton/Footer';

const Routing = () => (
  <Router>
    <div>
      <NavBar />
      <div className="main-container">
        <ErrorBoundary>
          <Suspense fallback={<Loader />}>
            <Switch>
              <Route exact path="/" component={Home} />
              <Route path="/about" component={About} />
              <Route path="/faq" component={Faq} />
              <Route path="*" component={Page404} />
              {/*<Route path="/topics" component={Topics} />
              <Route path="*" component={NoMatch} />*/}
            </Switch>
          </Suspense>
        </ErrorBoundary>
        <Footer />
      </div>
    </div>
  </Router>
);

export default Routing;
