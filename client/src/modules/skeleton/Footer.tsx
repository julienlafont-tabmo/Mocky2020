import React from 'react';
import { NavLink } from 'react-router-dom';

const Footer = () => (
  <footer className="text-center-xs space--xs">
    <div className="container">
      <div className="row">
        <div className="col-sm-7">
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
              <a href="#">
                <span className="h6 type--uppercase">
                  <p>You like it? Buy me a beer!</p>
                </span>
              </a>
            </li>
          </ul>
        </div>
        <div className="col-sm-5 text-right text-center-xs">
          <ul className="social-list list-inline list--hover">
            <li>
              <a href="#">
                <i className="socicon socicon-twitter icon icon--xs"></i>
              </a>
            </li>
          </ul>
        </div>
      </div>
      <div className="row">
        <div className="col-sm-7">
          <span className="type--fine-print">
            © <span className="update-year">2020</span>&nbsp;Mocky.io
          </span>
          <a className="type--fine-print" href="#">
            Privacy Policy
          </a>
          <a className="type--fine-print" href="#"></a>
        </div>
        <div className="col-sm-5 text-right text-center-xs">
          <a className="type--fine-print" href="#"></a>
        </div>
      </div>
    </div>
  </footer>
);

export default Footer;
