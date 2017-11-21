(function() {
    'use strict';
    angular
        .module('securityalarmApp')
        .factory('Status', Status);

    Status.$inject = ['$resource'];

    function Status ($resource) {
        var resourceUrl =  'api/statuses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'video': { method: 'GET',  params: {id: 'video.mp4'}},
            'images': { method: 'GET', isArray: true,  params: {id: 'images'}},
            'locations': { method: 'GET', isArray: true, params: {id: 'locations'}}
        });
    }
})();
