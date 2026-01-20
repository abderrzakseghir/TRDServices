FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 5001

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["MockScoreService.Web/MockScoreService.Web.csproj", "MockScoreService.Web/"]
COPY ["MockScoreService.API/MockScoreService.API.csproj", "MockScoreService.API/"]
RUN dotnet restore "MockScoreService.Web/MockScoreService.Web.csproj"
COPY . .
WORKDIR "/src/MockScoreService.Web"
RUN dotnet build "MockScoreService.Web.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "MockScoreService.Web.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MockScoreService.Web.dll"]
