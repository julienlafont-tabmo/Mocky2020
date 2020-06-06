import React from 'react';
import { NavLink } from 'react-router-dom';

export default () => (
  <footer className="text-center-xs space--xs">
    <div className="container">
      <div className="row">
        <div className="col-sm-6">
          <ul className="list-inline">
            <li>
              <NavLink to="/about">
                <span className="h6 type--uppercase">
                  <p>About</p>
                </span>
              </NavLink>
            </li>
            <li>
              <NavLink to="/faq">
                <span className="h6 type--uppercase">
                  <p>FAQ</p>
                </span>
              </NavLink>
            </li>
            <li>
              <a href="https://www.buymeacoffee.com/julienlafont">
                <span className="h6 type--uppercase">
                  <p>
                    You like it? Buy me a <span className="type--strikethrough">beer</span> vegetable 🥦.
                  </p>
                </span>
              </a>
            </li>
          </ul>
        </div>
        <div className="col-sm-6 text-right text-center-xs">
          <ul className="social-list list-inline list--hover">
            <li>
              <a href="https://www.twitter.com/julien_lafont" rel="noopener noreferrer" target="_blank">
                Made with <code>love</code> by @julien_lafont <i className="socicon socicon-twitter icon icon--xs"></i>
              </a>
            </li>
          </ul>
        </div>
      </div>
      <div className="row">
        <div className="col-sm-4">
          <span className="type--fine-print">
            © <span className="update-year">2020</span> Mocky.io
          </span>
          <NavLink to="/faq" className="type--fine-print">
            Privacy Policy
          </NavLink>
        </div>
      </div>
    </div>
  </footer>
);
