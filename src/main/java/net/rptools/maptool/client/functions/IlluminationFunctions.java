/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.client.functions;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.List;
import net.rptools.maptool.client.ui.zone.PlayerView;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.util.FunctionUtil;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableResolver;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.Function;

public class IlluminationFunctions extends AbstractFunction {
  private static final IlluminationFunctions instance = new IlluminationFunctions();

  private IlluminationFunctions() {
    super(0, Function.UNLIMITED_PARAMETERS, "getIllumination");
  }

  public static IlluminationFunctions getInstance() {
    return instance;
  }

  @Override
  public Object childEvaluate(
      Parser parser, VariableResolver resolver, String functionName, List<Object> parameters)
      throws ParserException {
    if (functionName.equalsIgnoreCase("getIllumination")) {
      return BigDecimal.valueOf(getIllumination(functionName, parameters));
    }

    throw new ParserException(I18N.getText("macro.function.general.unknownFunction", functionName));
  }

  private int getIllumination(String functionName, List<Object> parameters) throws ParserException {
    FunctionUtil.blockUntrustedMacro(functionName);
    FunctionUtil.checkNumberParam(functionName, parameters, 2, 3);
    final var x = FunctionUtil.paramAsInteger(functionName, parameters, 0, false);
    final var y = FunctionUtil.paramAsInteger(functionName, parameters, 1, false);
    final var renderer = FunctionUtil.getZoneRendererFromParam(functionName, parameters, 2);

    PlayerView playerView = renderer.getPlayerView();
    final var point = new Point2D.Double(x, y);

    for (final var lumensLevel :
        renderer.getZoneView().getDisjointObscuredLumensLevels(playerView)) {
      if (lumensLevel.darknessArea().contains(point)) {
        return -lumensLevel.lumensStrength();
      } else if (lumensLevel.lightArea().contains(point)) {
        return lumensLevel.lumensStrength();
      }
    }

    return 0;
  }
}
