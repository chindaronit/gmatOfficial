package com.gmat.ui.screens.rewards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gmat.R
import com.gmat.ui.components.CenterBar
import com.gmat.ui.components.RenderPainterIcon
import com.gmat.ui.theme.bronze
import com.gmat.ui.theme.gold
import com.gmat.ui.theme.silver

@Composable
fun Rewards(
    navController: NavController
) {
    Scaffold(
        topBar = {
            CenterBar(
                onClick = { navController.navigateUp() },
                title = {
                    Text(
                        text = stringResource(id = R.string.rewards),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RenderPainterIcon(id = R.drawable.reward_icon, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.onPrimary)
                    Column(
                        modifier = Modifier.padding(horizontal = 30.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.your_rank) + ": 501",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = stringResource(id = R.string.your_points) + ": 400",
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.leaderboard),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 15.dp, top = 20.dp, bottom = 10.dp)
                    )
                }

                LeaderboardEntry(
                    name = "Ronit Chinda",
                    points = "1000",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    position = 1
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
                LeaderboardEntry(
                    name = "Vishal Kumar Prajapati",
                    points = "996",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    position = 2
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
                LeaderboardEntry(
                    name = "Sahaj Gupta",
                    points = "995",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    position = 3
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
                LeaderboardEntry(
                    name = "Himanshu Ahirwal",
                    points = "992",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    position = 4
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
                LeaderboardEntry(
                    name = "Rishabh Dev",
                    points = "990",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    position = 5
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
                LeaderboardEntry(
                    name = "Anant Sharma",
                    points = "885",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    position = 6
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
            }
        }
    }
}

@Composable
fun LeaderboardEntry(name: String, points: String, modifier: Modifier = Modifier, position: Int) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
        ) {

            RenderPainterIcon(
                id = R.drawable.reward_icon, modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            3.dp,
                            when(position){
                                1-> gold
                                2-> silver
                                3-> bronze
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        ),
                        CircleShape
                    )
                    .padding(10.dp),
                tint=
                    when(position){
                        1-> gold
                        2-> silver
                        3-> bronze
                        else -> MaterialTheme.colorScheme.onSurface
                    }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp),
            ) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.widthIn(max = 150.dp)
                )
            }
            Text(
                text = points,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

