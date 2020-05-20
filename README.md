Throttle(1, 1.second)(
        /*GZip(CORS(Jsonp("callback")(*/ mockV2Service.mockRoutes <+> mockV3Service.mockRoutes /*)))*/ )(
        sync,
        timer.clock)