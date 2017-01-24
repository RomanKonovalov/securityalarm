(function () {
    'use strict';

    angular
        .module('securityalarmApp')
        .factory('Util', Util);

    Util.$inject = ['$resource'];

    function Util ($resource) {
        var service = $resource('api/utils/:type', {}, {
            'trackingTypes': {method: 'GET', isArray: true, params: {type: 'trackingTypes'}},
            'notificationTypes': {method: 'GET', isArray: true, params: {type: 'notificationTypes'}}
        });

        return service;
    }
})();
