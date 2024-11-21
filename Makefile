up:
	docker-compose --profile dev up -d --build
	docker rmi $$(docker images -f "dangling=true" -q)

down:
	docker compose --profile dev down