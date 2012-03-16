<?
  $n = (int)variable_get('uberdust_charts_number',1);
  $default = !empty($form_state['values']['uberdust_charts_sameroom']) ? $form_state['values']['uberdust_charts_sameroom'] : variable_get('uberdust_charts_sameroom','');
  if ($default ==1 && $n > 1)
  $room = 'inside '.variable_get('uberdust_charts_room', '');
  else
  $room = ' ';
  for( $i=1;$i<$n+1;$i++ ) {
  ${"node_".$i} = variable_get('uberdust_charts_nodeurn_'.$i, '');
  ${"nodeUrn_".$i} = variable_get('uberdust_charts_nodeurn_'.$i, '');
  ${"capabilityUrn_".$i} = variable_get('uberdust_charts_capability_'.$i, '');  
  ${"capability_".$i} =   variable_get('uberdust_charts_capability_'.$i, '');
  }
  $container=$node." ".$capability;
  $unit = "(lux)";
	  $maxRows = 1000;
//<?=$container?>
?>

   <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
    <script type="text/javascript">

        var chart;
        $(document).ready(function() {
		$(".item-list").attr('id',"item_list_id");
            chart = new Highcharts.Chart({
                chart: {
                   renderTo: 'container',
                    defaultSeriesType: 'spline',
                    zoomType: 'x',
                    spacingRight: 20
                },
                title: {
                    text: '<?=$capability_1?> chart <?= $room?>'  

                },    
                subtitle: {
			text:
		'<?php for( $i=1;$i<$n+1;$i++ ) {
                    echo "node ${'nodeUrn_'.$i}";
					if ($i != $n) {
						print(" , ");
					}
			 } ?>'
                },
                xAxis: {
                    type: 'datetime',
                    dateTimeLabelFormats: {
                        day: '%e %b',
                        month: '%e %b'
                    },
                    tickPixelInterval: 400,
                    maxZoom: 1000
                },
                yAxis: {
                    title: {
                        text: '<?= $capability_1." ".$unit ?>'
                    },  
                    min: 0.6,
                    startOnTick: false,
                    showFirstLabel: false
                },
                tooltip: {
                    shared: true
                },
                legend: {
                    enabled: false
                },
	      plotOptions: {
		 series: {
		    lineWidth: 1,
		    marker: {
		       enabled: false,
		       states: {
		          hover: {
		             enabled: true,
		             radius: 5
		          }
		       }
		    }
		 }
	      },
                series: [
				<?php	for( $i=1;$i<$n+1;$i++ ) { ?>
				    {
                        name: '<?= ${"nodeUrn_".$i}." ".${"capabilityUrn_".$i} ?> reading <?= $unit ?>',
                        data: [
<?php


$tabdelimited = file_get_contents("http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:".${"node_".$i}."/capability/urn:wisebed:node:capability:".${"capabilityUrn_".$i}."/tabdelimited/limit/".$maxRows);
$lines = explode("\n", $tabdelimited, $maxRows);
unset($lines[count($lines)-1]);

$firstRow = explode("\t", $lines[0]);
print("                                 [".$firstRow[0]." , ".$firstRow[1]."]");
unset($lines[0]);

foreach ($lines as $thisLine) {
  $row = explode("\t", $thisLine);
  print(",\n                                 [".$row[0]." , ".$row[1]."]");
}

?>
			      ]
                    }
					
				<?php 
					if ($i != $n) {
						print(",\n");
					}
					
				} 
				?>  <!-- end of for -->
                ]
            });
        });
    </script>



