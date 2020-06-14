#!/bin/bash

HOST_URL=127.0.0.1:5000

is_exist_image () {
    has = `docker images | grep $HOST_URL'/'$1`

    if [ -z "${has}" ]; then
       return 1
    else
        return 0
    fi
}

build_mysql () {
    sh ./mysql/build.sh
}

deploy_mysql () {
    is_exist_image 'rawa/mysql'

    if [ $? -eq "0" ]; then
        echo "mysql image has already deployed"
    else
        docker tag rawa/mysql $HOST_URL'/rawa/mysql'
    fi
}


