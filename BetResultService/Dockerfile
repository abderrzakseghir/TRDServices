# Use ASP.NET Core Runtime (or default runtime if worker)
FROM mcr.microsoft.com/dotnet/runtime:8.0 AS base
WORKDIR /app

# Use SDK for build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["BetResultService/BetResultService.csproj", "BetResultService/"]
RUN dotnet restore "BetResultService/BetResultService.csproj"
COPY . .
WORKDIR "/src/BetResultService"
RUN dotnet build "BetResultService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "BetResultService.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "BetResultService.dll"]
