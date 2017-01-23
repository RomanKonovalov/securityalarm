(function () {
    'use strict';

    angular
        .module('securityalarmApp')
        .factory('Device', Device);

    Device.$inject = ['$resource'];

    function Device ($resource) {
        var service = $resource('api/devices/:action', {}, {
            'query': {method: 'GET', isArray: true},
            'activate': {method: 'POST', params: {action: 'activate'}}
        });

        return service;
    }
})();
