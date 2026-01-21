ping-all:
    echo "Ping all instances inside inventory.ini"
    ansible -i inventory.ini all -m ping
nfs-server:
    echo "Setting up NFS Server...! 😀"
    ansible-playbook -i inventory.ini playbooks/nfs-server.yaml
nfs-client:
    echo "Setting up NFS Client...! 😀"
    ansible-playbook -i inventory.ini playbooks/nfs-client.yaml
run-spring-compose:
    echo "Run Spring Docker Compose...! 😀"
    ansible-playbook -i inventory.ini playbooks/run_spring_compose.yaml