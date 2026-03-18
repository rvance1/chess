package dto;

import java.util.List;

import model.GameData;

public record ListGamesResult(List<GameData> games) {}