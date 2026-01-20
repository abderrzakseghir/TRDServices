# Use ASP.NET Core Runtime (or default runtime if worker)
FROM mcr.microsoft.com/dotnet/runtime:8.0 AS base
WORKDIR /app

# Use SDK for build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["ScoreService/ScoreService.csproj", "ScoreService/"]
RUN dotnet restore "ScoreService/ScoreService.csproj"
COPY . .
WORKDIR "/src/ScoreService"
RUN dotnet build "ScoreService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "ScoreService.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "ScoreService.dll"]
