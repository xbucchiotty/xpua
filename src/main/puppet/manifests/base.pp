exec { 'apt-get update':
    command     => '/usr/bin/apt-get update',
    refreshonly => true,
    before      => [Package['mongodb-10gen'],Package['openjdk-6-jdk']]
}


exec {'apt-key 10gen':
    command     => '/usr/bin/apt-key adv --keyserver keyserver.ubuntu.com --recv 7F0CEB10',
    refreshonly => true,
    notify      => Exec['apt-get update'],
}

file{'/etc/apt/sources.list.d/10gen.list':
    ensure      => present,
    content     => 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen',
    notify      => Exec['apt-key 10gen']
}

package{'mongodb-10gen':
    ensure      => present,
}

service{'mongodb':
    ensure      => running,
    require     => Package['mongodb-10gen'],
}

package{'openjdk-6-jdk':
    ensure      => present,

}