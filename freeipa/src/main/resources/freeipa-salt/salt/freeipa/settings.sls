{% set freeipa = {} %}
     {% set backup_location = salt['pillar.get']('freeipa:backup_location') %}
     {% set backup_platform = salt['pillar.get']('freeipa:backup_platform') %}
     {% set azure_instance_msi = salt['pillar.get']('freeipa:azure_instance_msi') %}
     {% set hostname = salt['grains.get']('fqdn') %}

     {% do freeipa.update({
         "backup_platform" : backup_platform,
         "backup_location" : backup_location,
         "hostname": hostname,
         "azure_instance_msi": azure_instance_msi
     }) %}
