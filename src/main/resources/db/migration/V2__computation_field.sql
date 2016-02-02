/**
 * Author:  Raphaël Calabro (raphael.calabro@netapsys.fr)
 * Created: 2 févr. 2016
 */

-- "code" property.
ALTER TABLE flexible_element ADD code VARCHAR(30);

-- "computation field" flexible element.

CREATE TABLE computation_element
(
  id_flexible_element int primary key,
  rule varchar(1500),
  minimum varchar(1500),
  maximum varchar(1500)
);
ALTER TABLE computation_element OWNER TO sigmah;
