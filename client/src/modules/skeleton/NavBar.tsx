import React from 'react';
import { NavLink } from 'react-router-dom';

const NavBar = () => (
  <div className="nav-container">
    <div>
      <nav id="menu1" className="bar bar-1 hidden-xs">
        <div className="container">
          <div className="row">
            <div className="col-lg-1 hidden-xs col-md-3">
              {/*}
              <div className="bar__module">
                <a href="index.html">
                  <img className="logo logo-dark" alt="logo" src="img/logo4.png" />
                  <img className="logo logo-light" alt="logo" src="img/logo-light.png" />
                </a>
              </div>
              */}
            </div>
            <div className="col-lg-11 col-md-12 text-right text-left-xs text-left-sm">
              <div className="bar__module">
                <NavLink to="/manage" className="btn btn--sm type--uppercase">
                  <span className="btn__text">Manage my mocks</span>
                </NavLink>

                <NavLink to="/create" className="btn btn--sm type--uppercase btn--primary">
                  <span className="btn__text" data-tooltip="Tooltip text here">
                    New mock
                  </span>
                </NavLink>
              </div>
            </div>
          </div>
        </div>
      </nav>
    </div>
  </div>
);

export default NavBar;
