FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 5000

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["MockScoreService.API/MockScoreService.API.csproj", "MockScoreService.API/"]
RUN dotnet restore "MockScoreService.API/MockScoreService.API.csproj"
COPY . .
WORKDIR "/src/MockScoreService.API"
RUN dotnet build "MockScoreService.API.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "MockScoreService.API.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MockScoreService.API.dll"]
