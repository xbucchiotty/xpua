# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |config|
    config.vm.define :dev do |config|
        config.vm.box="lucid32-fr"
        config.vm.host_name="base"
        config.vm.network :hostonly, "192.168.56.201"
        config.vm.forward_port 27017, 27017
        config.vm.customize do |config|
            config.memory_size = "1024"
        end
        config.vm.provision :puppet do |puppet|
            puppet.manifests_path = "src/main/puppet/manifests"
            puppet.manifest_file = "base.pp"
        end
    end

end
