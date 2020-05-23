import React from 'react';
import { NavLink } from 'react-router-dom';

const Features = () => (
  <>
    <a id="features" className="in-page-link"></a>
    <section className="space--xxs">
      <div className="container">
        <div className="row">
          <div className="col-md-6 col-lg-3">
            <div className="feature feature-6">
              <i className="icon color--primary icon-Money-2 icon--sm"></i>
              <h5>Free &amp; Unlimited</h5>
              <p>
                Mocky is free to use, no ads, no hidden subscriptions or service limits.&nbsp;Your mocks will be
                available <b>forever</b> (just call it one time per year), but without any{' '}
                <a href="http://www.toto.fr">guarantee</a>.<br />
              </p>
            </div>
          </div>
          <div className="col-md-6 col-lg-3">
            <div className="feature feature-6">
              <i className="icon color--primary icon-Box-Full icon--sm"></i>
              <h5>Total control</h5>
              <p>
                New in Mocky, you can now update or delete your mocks at any time.
                <br />
                The next release will go further with sign-in feature and cloud-based mock management
              </p>
              <span className="label">NEW</span>
            </div>
          </div>
          <div className="col-md-6 col-lg-3">
            <div className="feature feature-6">
              <i className="icon color--primary icon-Chemical icon--sm"></i>
              <h5>Developper Friendly</h5>
              <p>
                Mocky is compatible with JS, Mobile and Server applications, featuring CORS, JSONP and GZIP responses.
                <br />
                No authentication, just call it!
              </p>
            </div>
          </div>
          <div className="col-md-6 col-lg-3">
            <div className="feature feature-6">
              <i className="icon color--primary icon-Code-Window icon--sm"></i>
              <h5>Open Source&nbsp;</h5>
              <p>
                {' '}
                Mocky is distributed with Apache 2 licence on Github. Ready-to-use distributions are available to host
                your own Mocky instance.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </>
);

export default Features;
