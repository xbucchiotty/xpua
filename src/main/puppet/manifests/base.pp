exec { 'apt-get update':
    command     => '/usr/bin/apt-get update',
    refreshonly => true,
    before      => [Package['mongodb-10gen'],Package['openjdk-6-jdk'],Package['neo4j']]
}

exec {'apt-key 10gen':
    command     => '/usr/bin/apt-key adv --keyserver keyserver.ubuntu.com --recv 7F0CEB10',
    refreshonly => true,
    before      => Exec['apt-get update'],
    notify      => Exec['apt-get update'],
}

exec {'key neo4j':
    command     => '/usr/bin/wget -O - http://debian.neo4j.org/neotechnology.gpg.key | /usr/bin/apt-key add - ',
    refreshonly => true,
    before      => Exec['apt-get update'],
    notify      => Exec['apt-get update'],
}

file{'/etc/apt/sources.list.d/10gen.list':
    ensure      => present,
    content     => 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen',
    before      => Exec['apt-key 10gen'],
    notify      => Exec['apt-key 10gen'],
}

file{'/etc/apt/sources.list.d/neo4j.list':
    ensure      => present,
    content     => 'deb http://debian.neo4j.org/repo stable/',
    before      => Exec['key neo4j'],
    notify      => Exec['key neo4j'],
}

package{'mongodb-10gen':
    ensure      => present,
}

package{'neo4j':
    ensure      => present,
}

service{'neo4j-service':
    ensure      => running,
    require     => Package['neo4j'],
}

service{'mongodb':
    ensure      => running,
    require     => Package['mongodb-10gen'],
}

package{'openjdk-6-jdk':
    ensure      => present,

}
